'use strict';

describe('Controller Tests', function() {

    describe('Pays Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPays, MockEcole, MockRegion;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPays = jasmine.createSpy('MockPays');
            MockEcole = jasmine.createSpy('MockEcole');
            MockRegion = jasmine.createSpy('MockRegion');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Pays': MockPays,
                'Ecole': MockEcole,
                'Region': MockRegion
            };
            createController = function() {
                $injector.get('$controller')("PaysDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'smartschoolApp:paysUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
