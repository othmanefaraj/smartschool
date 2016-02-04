 'use strict';

angular.module('smartschoolApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-smartschoolApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-smartschoolApp-params')});
                }
                return response;
            }
        };
    });
