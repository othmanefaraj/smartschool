'use strict';

describe('Controller Tests', function() {

    describe('Filiere Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockFiliere, MockClasseType;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockFiliere = jasmine.createSpy('MockFiliere');
            MockClasseType = jasmine.createSpy('MockClasseType');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Filiere': MockFiliere,
                'ClasseType': MockClasseType
            };
            createController = function() {
                $injector.get('$controller')("FiliereDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'smartschoolApp:filiereUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
