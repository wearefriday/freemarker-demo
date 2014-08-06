/**
 *  This JS is what we will use to precompile our plugins
 *  for most cases, we will list our modules here as we want to precompile most everything
 *  only in exception cases, will we allow for dynamic loading of modules and other assets
 */

define(function (require) {
    'use strict';

    // bower
    require('bsp-autosubmit');

    return this;

});
