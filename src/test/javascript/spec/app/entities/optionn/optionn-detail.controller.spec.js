'use strict';

describe('Controller Tests', function() {

    describe('Optionn Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockOptionn, MockClasseType;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockOptionn = jasmine.createSpy('MockOptionn');
            MockClasseType = jasmine.createSpy('MockClasseType');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Optionn': MockOptionn,
                'ClasseType': MockClasseType
            };
            createController = function() {
                $injector.get('$controller')("OptionnDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'smartschoolApp:optionnUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
