'use strict';

angular.module('smartschoolApp')
    .controller('EcoleDetailController', function ($scope, $rootScope, $stateParams, entity, Ecole, User, Pays, Region, Ville, ClasseType) {
        $scope.ecole = entity;
        $scope.load = function (id) {
            Ecole.get({id: id}, function(result) {
                $scope.ecole = result;
            });
        };
        var unsubscribe = $rootScope.$on('smartschoolApp:ecoleUpdate', function(event, result) {
            $scope.ecole = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
