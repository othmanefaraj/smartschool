'use strict';

angular.module('smartschoolApp')
    .factory('Ecole', function ($resource, DateUtils) {
        return $resource('api/ecoles/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
