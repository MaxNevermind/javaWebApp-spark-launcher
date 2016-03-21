'use strict';

angular.module('sparkWordsCounterApp')
    .controller('MainController', function ($scope, $http, $interval) {

        $scope.runSparkJob = function() {
            $http.post('getWordsCounter', {hdfsInFilePath: $scope.hdfsInFilePath}).then(function (response) {
            });
        }

        var refreshJobResults = function() {
            $http.get('getJobResults').then(function (response) {
                $scope.jobResults = response.data;
            });
        }

        var interval = $interval(function () {
            refreshJobResults();
        }, 1000);

        $scope.$on('$destroy', function() {
            $interval.cancel(interval);
        });

    });
