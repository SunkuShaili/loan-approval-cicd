module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine'],
    plugins: [
        require('karma-jasmine'),
        require('karma-chrome-launcher'),
        require('karma-jasmine-html-reporter'),
        require('karma-coverage')
        ],

        reporters: ['progress', 'kjhtml', 'coverage'],

        coverageReporter: {
        dir: require('path').join(__dirname, 'coverage'),
        subdir: '.',
        reporters: [
            { type: 'html' },
            { type: 'text-summary' }
        ]
    },

    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: ['Chrome'],
    singleRun: false,
    restartOnFileChange: true
  });
};
