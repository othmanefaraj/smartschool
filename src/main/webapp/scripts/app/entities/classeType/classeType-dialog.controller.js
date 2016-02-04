'use strict';

angular.module('smartschoolApp').controller('ClasseTypeDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'ClasseType', 'TypeEnseignement', 'Cycle', 'Niveau', 'Filiere', 'Serie', 'Optionn',
        function($scope, $stateParams, $uibModalInstance, entity, ClasseType, TypeEnseignement, Cycle, Niveau, Filiere, Serie, Optionn) {

        $scope.classeType = entity;
        $scope.typeenseignements = TypeEnseignement.query();
        $scope.cycles = Cycle.query();
        $scope.niveaus = Niveau.query();
        $scope.filieres = Filiere.query();
        $scope.series = Serie.query();
        $scope.optionns = Optionn.query();
        $scope.load = function(id) {
            ClasseType.get({id : id}, function(result) {
                $scope.classeType = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('smartschoolApp:classeTypeUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.classeType.id != null) {
                ClasseType.update($scope.classeType, onSaveSuccess, onSaveError);
            } else {
                ClasseType.save($scope.classeType, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForDateCreation = {};

        $scope.datePickerForDateCreation.status = {
            opened: false
        };

        $scope.datePickerForDateCreationOpen = function($event) {
            $scope.datePickerForDateCreation.status.opened = true;
        };
}]);
