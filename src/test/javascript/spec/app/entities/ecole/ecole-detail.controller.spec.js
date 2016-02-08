'use strict';

describe('Controller Tests', function() {

    describe('Ecole Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockEcole, MockUser, MockPays, MockRegion, MockVille, MockClasseType;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockEcole = jasmine.createSpy('MockEcole');
            MockUser = jasmine.createSpy('MockUser');
            MockPays = jasmine.createSpy('MockPays');
            MockRegion = jasmine.createSpy('MockRegion');
            MockVille = jasmine.createSpy('MockVille');
            MockClasseType = jasmine.createSpy('MockClasseType');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Ecole': MockEcole,
                'User': MockUser,
                'Pays': MockPays,
                'Region': MockRegion,
                'Ville': MockVille,
                'ClasseType': MockClasseType
            };
            createController = function() {
                $injector.get('$controller')("EcoleDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'smartschoolApp:ecoleUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
