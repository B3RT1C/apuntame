function fn() {
  var env = karate.env || 'local';
  var config = {
    baseUrl: 'http://localhost:8080',
    adminUser: 'admin',
    adminPass: 'admin',
    waiterUser: 'camarero1',
    waiterPass: 'camarero1'
  };

  if (env === 'docker') {
    config.baseUrl = 'http://localhost:8080';
  }

  karate.configure('url', config.baseUrl);
  karate.log('Karate env:', env, '| baseUrl:', config.baseUrl);
  return config;
}
