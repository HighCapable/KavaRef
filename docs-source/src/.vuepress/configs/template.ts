import { i18n } from './utils';

interface PageLinkRefs {
    dev: Record<string, string>[];
    prod: Record<string, string>[];
}

const navigationLinks = {
    start: [
        '/guide/home',
        '/guide/quick-start'
    ],
    library: [
        '/library/kavaref-bom',
        '/library/kavaref-core',
        '/library/kavaref-android',
        '/library/kavaref-jvm',
        '/library/kavaref-extension'
    ],
    config: [
        '/config/r8-proguard',
        '/config/processor-resolvers',
        '/config/migration',
        '/config/lint-rules'
    ],
    about: [
        '/about/changelog',
        '/about/future',
        '/about/contacts',
        '/about/about'
    ]
};

export const configs = {
    dev: {
        dest: 'dist',
        port: 9000
    },
    website: {
        base: '/KavaRef/',
        icon: '/KavaRef/images/logo.svg',
        logo: '/images/logo.svg',
        title: 'KavaRef',
        locales: {
            '/en/': {
                lang: 'en-US',
                description: 'A modernizing Java Reflection with Kotlin'
            },
            '/zh-cn/': {
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

export const navBarItems = {
    '/en/': [{
        text: 'Navigation',
        children: [{
            text: 'Get Started',
            children: i18n.array(navigationLinks.start, 'en')
        }, {
            text: 'Libraries',
            children: i18n.array(navigationLinks.library, 'en')
        }, {
            text: 'Configs',
            children: i18n.array(navigationLinks.config, 'en')
        }, {
            text: 'About',
            children: i18n.array(navigationLinks.about, 'en')
        }]
    }, {
        text: 'Contact Us',
        link: i18n.string(navigationLinks.about[2], 'en')
    }],
    '/zh-cn/': [{
        text: '导航',
        children: [{
            text: '入门',
            children: i18n.array(navigationLinks.start, 'zh-cn')
        }, {
            text: '依赖',
            children: i18n.array(navigationLinks.library, 'zh-cn')
        }, {
            text: '配置',
            children: i18n.array(navigationLinks.config, 'zh-cn')
        }, {
            text: '关于',
            children: i18n.array(navigationLinks.about, 'zh-cn')
        }]
    }, {
        text: '联系我们',
        link: i18n.string(navigationLinks.about[2], 'zh-cn')
    }]
};

export const sideBarItems = {
    '/en/': [{
        text: 'Get Started',
        collapsible: true,
        children: i18n.array(navigationLinks.start, 'en')
    }, {
        text: 'Libraries',
        collapsible: true,
        children: i18n.array(navigationLinks.library, 'en')
    }, {
        text: 'Configs',
        collapsible: true,
        children: i18n.array(navigationLinks.config, 'en')
    }, {
        text: 'About',
        collapsible: true,
        children: i18n.array(navigationLinks.about, 'en')
    }],
    '/zh-cn/': [{
        text: '入门',
        collapsible: true,
        children: i18n.array(navigationLinks.start, 'zh-cn')
    }, {
        text: '依赖',
        collapsible: true,
        children: i18n.array(navigationLinks.library, 'zh-cn')
    }, {
        text: '配置',
        collapsible: true,
        children: i18n.array(navigationLinks.config, 'zh-cn')
    }, {
        text: '关于',
        collapsible: true,
        children: i18n.array(navigationLinks.about, 'zh-cn')
    }]
};