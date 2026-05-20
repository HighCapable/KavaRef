import type MarkdownIt from 'markdown-it';
import { resolveI18nLink } from './anchors';

export const env = {
    dev: process.env.NODE_ENV === 'development'
};

export const i18n = {
    space: ' ',
    string: (content: string, locale: string) => {
        return '/' + locale + content;
    },
    array: (contents: string[], locale: string) => {
        const newContents: string[] = [];
        contents.forEach((content) => {
            newContents.push(i18n.string(content, locale));
        });
        return newContents;
    }
};

export const markdown = {
    injectLinks: (md: MarkdownIt, maps: Record<string, string>[]) => {
        const defaultRender = md.renderer.rules.link_open || function (tokens, idx, options, _env, self) {
            return self.renderToken(tokens, idx, options);
        };
        md.renderer.rules.link_open = function (tokens, idx, options, env, self) {
            const hrefIndex = tokens[idx].attrIndex('href');
            if (hrefIndex < 0 || !tokens[idx].attrs) {
                return defaultRender(tokens, idx, options, env, self);
            }
            let current = tokens[idx].attrs[hrefIndex][1];
            current = resolveI18nLink({
                base: env.base,
                filePathRelative: env.filePathRelative
            }, current);
            for (const map of maps) {
                for (const [search, replace] of Object.entries(map)) {
                    if (current.startsWith(search)) {
                        current = current.replace(search, replace);
                        tokens[idx].attrs[hrefIndex][1] = current;
                        break;
                    }
                }
            }
            tokens[idx].attrs[hrefIndex][1] = current;
            return defaultRender(tokens, idx, options, env, self);
        };
    }
};