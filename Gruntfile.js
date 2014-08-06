module.exports = function(grunt) {
    require('bsp-grunt')(grunt, {
        bsp: {
            styles: {
                dir: "/assets/style",
                less: [ "freemarker.less" ]
            },
            scripts: {
                dir: "/assets/script",
                rjsModules: [
                    {
                        "name": "freemarker"
                    }
                ]
            }
        }
    });
};
