'use strict';

describe('Controller Tests', function() {

    describe('TypeEnseignement Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockTypeEnseignement, MockClasseType;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockTypeEnseignement = jasmine.createSpy('MockTypeEnseignement');
            MockClasseType = jasmine.createSpy('MockClasseType');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'TypeEnseignement': MockTypeEnseignement,
                'ClasseType': MockClasseType
            };
            createController = function() {
                $injector.get('$controller')("TypeEnseignementDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'smartschoolApp:typeEnseignementUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
