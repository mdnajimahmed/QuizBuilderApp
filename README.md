# Live URI:
- https://d3dsh9jnfe1rsv.cloudfront.net

# Demo:
- A short demo can be found in the folder demo.

# API introduction:
- To get an overview of the APIs please read api.md file from the doc folder.
- To know the configurations for the application please read configuration.md file from the doc folder.
- To test the APIs from the remote server please use the postman collection inside the doc folder.

# Running the system locally:
- The system uses port 8080 when it runs in local. We need to ensure that port 8080 is not in use in local.
- To run the system locally using docker-compose, please use command `docker-compose up` from the root directory.
- One way to debug/edit the code is to use intellij, we need to provide the following parameters in the environment variables before we can proceed - 
`DB_HOST=<TO-BE-UPDATED>;DB_NAME=<TO-BE-UPDATED>;DB_PASSWORD=<TO-BE-UPDATED>;DB_USERNAME=<TO-BE-UPDATED>;POOL_ID=ap-southeast-2_5Xy5gOXtg;WEB_CLIENT_ID=3e074qfjrs0ba81g4p8tthhk5u;WEB_REDIRECT_URI=http://localhost:8080/welcome`

# Running the tests locally:
- To run the tests we also need to provide WEB_CLIENT_SECRET in the environment variable which can be obtained by emailing at najim.ju@gmail.com.