'use strict';

describe('Controller Tests', function() {

    describe('Region Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockRegion, MockPays, MockVille, MockEcole;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockRegion = jasmine.createSpy('MockRegion');
            MockPays = jasmine.createSpy('MockPays');
            MockVille = jasmine.createSpy('MockVille');
            MockEcole = jasmine.createSpy('MockEcole');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Region': MockRegion,
                'Pays': MockPays,
                'Ville': MockVille,
                'Ecole': MockEcole
            };
            createController = function() {
                $injector.get('$controller')("RegionDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'smartschoolApp:regionUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
