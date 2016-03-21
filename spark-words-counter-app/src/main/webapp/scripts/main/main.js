'use strict';

angular.module('sparkWordsCounterApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('home', {
                url: '/',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/main/main.html',
                        controller: 'MainController'
                    }
                },
                resolve: {
                    
                }
            });
    });
