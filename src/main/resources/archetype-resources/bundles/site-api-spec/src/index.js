import SwaggerUI from 'swagger-ui'
import 'swagger-ui/dist/swagger-ui.css';

const spec = require('./spec/site-api.yaml');

const ui = SwaggerUI({
  spec,
  dom_id: '#swagger',
});

ui.initOAuth({
  appName: 'Site API Spec',
  // See https://demo.identityserver.io/ for configuration details.
  clientId: 'implicit'
});
