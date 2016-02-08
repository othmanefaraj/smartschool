'use strict';

describe('Controller Tests', function() {

    describe('Ville Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockVille, MockRegion, MockEcole;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockVille = jasmine.createSpy('MockVille');
            MockRegion = jasmine.createSpy('MockRegion');
            MockEcole = jasmine.createSpy('MockEcole');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Ville': MockVille,
                'Region': MockRegion,
                'Ecole': MockEcole
            };
            createController = function() {
                $injector.get('$controller')("VilleDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'smartschoolApp:villeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
