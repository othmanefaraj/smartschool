'use strict';

angular.module('smartschoolApp').controller('EcoleDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Ecole', 'User', 'Pays', 'Region', 'Ville', 'ClasseType',
        function($scope, $stateParams, $uibModalInstance, entity, Ecole, User, Pays, Region, Ville, ClasseType) {

        $scope.ecole = entity;
        $scope.users = User.query();
        $scope.payss = Pays.query();
        $scope.regions = Region.query();
        $scope.villes = Ville.query();
        $scope.classetypes = ClasseType.query();
        $scope.load = function(id) {
            Ecole.get({id : id}, function(result) {
                $scope.ecole = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('smartschoolApp:ecoleUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.ecole.id != null) {
                Ecole.update($scope.ecole, onSaveSuccess, onSaveError);
            } else {
                Ecole.save($scope.ecole, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
