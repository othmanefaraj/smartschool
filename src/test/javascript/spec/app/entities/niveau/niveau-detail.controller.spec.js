'use strict';

describe('Controller Tests', function() {

    describe('Niveau Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockNiveau, MockClasseType;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockNiveau = jasmine.createSpy('MockNiveau');
            MockClasseType = jasmine.createSpy('MockClasseType');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Niveau': MockNiveau,
                'ClasseType': MockClasseType
            };
            createController = function() {
                $injector.get('$controller')("NiveauDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'smartschoolApp:niveauUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
