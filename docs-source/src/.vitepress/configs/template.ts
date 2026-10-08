type Locale = 'en' | 'zh-cn';

interface PageLinkRefs {
    dev: Record<string, string>[];
    prod: Record<string, string>[];
}

interface NavigationLink {
    path: string;
    title: Record<Locale, string>;
}

interface NavigationSection {
    title: Record<Locale, string>;
    links: NavigationLink[];
}

const navigationSections: NavigationSection[] = [{
    title: { en: 'Get Started', 'zh-cn': '入门' },
    links: [
        { path: '/guide/home', title: { en: 'Introduction', 'zh-cn': '介绍' } },
        { path: '/guide/quick-start', title: { en: 'Quick Start', 'zh-cn': '快速开始' } }
    ]
}, {
    title: { en: 'Libraries', 'zh-cn': '依赖' },
    links: [
        { path: '/library/kavaref-bom', title: { en: 'kavaref-bom', 'zh-cn': 'kavaref-bom' } },
        { path: '/library/kavaref-core', title: { en: 'kavaref-core', 'zh-cn': 'kavaref-core' } },
        { path: '/library/kavaref-android', title: { en: 'kavaref-android', 'zh-cn': 'kavaref-android' } },
        { path: '/library/kavaref-jvm', title: { en: 'kavaref-jvm', 'zh-cn': 'kavaref-jvm' } },
        { path: '/library/kavaref-extension', title: { en: 'kavaref-extension', 'zh-cn': 'kavaref-extension' } }
    ]
}, {
    title: { en: 'Configs', 'zh-cn': '配置' },
    links: [
        { path: '/config/r8-proguard', title: { en: 'R8 & ProGuard Obfuscation', 'zh-cn': 'R8 与 Proguard 混淆' } },
        { path: '/config/runtime-loggers', title: { en: 'Third-party Loggers', 'zh-cn': '第三方日志记录器' } },
        { path: '/config/processor-resolvers', title: { en: 'Third-party Member Resolvers', 'zh-cn': '第三方 Member 解析器' } },
        { path: '/config/resolver-handlers', title: { en: 'Third-party Member Handlers', 'zh-cn': '第三方 Member 处理器' } },
        { path: '/config/migration', title: { en: 'Migration to KavaRef', 'zh-cn': '迁移至 KavaRef' } },
        { path: '/config/lint-rules', title: { en: 'Lint Rules', 'zh-cn': 'Lint 静态检查规范' } }
    ]
}, {
    title: { en: 'About', 'zh-cn': '关于' },
    links: [
        { path: '/about/changelog', title: { en: 'Changelog', 'zh-cn': '更新日志' } },
        { path: '/about/future', title: { en: 'Looking Toward the Future', 'zh-cn': '展望未来' } },
        { path: '/about/contacts', title: { en: 'Contact Us', 'zh-cn': '联系我们' } },
        { path: '/about/about', title: { en: 'About This Document', 'zh-cn': '关于此文档' } }
    ]
}];

const topNavigationLinks: NavigationLink[] = [
    { path: '/', title: { en: 'Home', 'zh-cn': '首页' } },
    { path: '/guide/quick-start', title: { en: 'Quick Start', 'zh-cn': '快速开始' } },
    { path: '/about/changelog', title: { en: 'Changelog', 'zh-cn': '更新日志' } },
    { path: '/about/contacts', title: { en: 'Contact Us', 'zh-cn': '联系我们' } }
];

const localizedLink = (link: NavigationLink, locale: Locale) => ({
    text: link.title[locale],
    link: `/${locale}${link.path}`
});

/** Creates the VitePress navigation and sidebar for the requested locale. */
export const createThemeNavigation = (locale: Locale) => {
    const sections = navigationSections.map((section) => ({
        text: section.title[locale],
        items: section.links.map((link) => localizedLink(link, locale))
    }));
    return {
        nav: topNavigationLinks.map((link) => localizedLink(link, locale)),
        sidebar: {
            [`/${locale}/`]: sections.map((section) => ({
                text: section.text,
                collapsed: false,
                items: section.items
            }))
        }
    };
};

/** Defines shared site, development server, and repository settings. */
export const configs = {
    dev: {
        dest: '../dist',
        port: 9000
    },
    website: {
        base: '/KavaRef/',
        icon: '/KavaRef/images/logo.svg',
        logo: '/images/logo.svg',
        title: 'KavaRef',
        locales: {
            en: {
                lang: 'en-US',
                description: 'A modernizing Java Reflection with Kotlin'
            },
            'zh-cn': {
                lang: 'zh-CN',
                description: '一个使用 Kotlin 实现的现代化 Java 反射'
            }
        }
    },
    github: {
        repo: 'https://github.com/HighCapable/KavaRef',
        page: 'https://highcapable.github.io/KavaRef',
        branch: 'main',
        dir: 'docs-source/src'
    }
};

/** Defines custom Markdown link protocol replacements for each build mode. */
export const pageLinkRefs: PageLinkRefs = {
    dev: [
        { 'repo://': `${configs.github.repo}/` },
        // KDoc URL for local debugging, non-fixed value, adjust according to your own needs.
        // You can run ./build-dokka.sh and start the local server in dist/KDoc.
        { 'kdoc://': 'http://localhost:9001/' }
    ],
    prod: [
        { 'repo://': `${configs.github.repo}/` },
        { 'kdoc://': `${configs.github.page}/KDoc/` }
    ]
};