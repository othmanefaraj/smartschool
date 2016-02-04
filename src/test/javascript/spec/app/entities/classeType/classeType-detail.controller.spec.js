'use strict';

describe('Controller Tests', function() {

    describe('ClasseType Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockClasseType, MockTypeEnseignement, MockCycle, MockNiveau, MockFiliere, MockSerie, MockOptionn;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockClasseType = jasmine.createSpy('MockClasseType');
            MockTypeEnseignement = jasmine.createSpy('MockTypeEnseignement');
            MockCycle = jasmine.createSpy('MockCycle');
            MockNiveau = jasmine.createSpy('MockNiveau');
            MockFiliere = jasmine.createSpy('MockFiliere');
            MockSerie = jasmine.createSpy('MockSerie');
            MockOptionn = jasmine.createSpy('MockOptionn');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ClasseType': MockClasseType,
                'TypeEnseignement': MockTypeEnseignement,
                'Cycle': MockCycle,
                'Niveau': MockNiveau,
                'Filiere': MockFiliere,
                'Serie': MockSerie,
                'Optionn': MockOptionn
            };
            createController = function() {
                $injector.get('$controller')("ClasseTypeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'smartschoolApp:classeTypeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
