# Image-Repository

This repository is my submission for Shopify's Developer Intern Challenge question, as part of my application.

**TASK: Build an Image Repository.**

####Technologies and Frameworks used:
- Spring Boot 
- Kotlin
- Spring Security
- [JSON Web Token](https://github.com/jwtk/jjwt)
- Postgresql (database)
- Cloudinary (storage)
- Heroku (deployment)
- Maven (build tool)

####Features:
1. JWT Authentication ( Register => Login ).
2. Secure uploading and storing of images.
3. Viewing all images.
4. Filter images by category and/or by user who uploaded them.
5. Secure deletion of images.
6. Access control (prevent a user deleting images from another user).

####Setting up Project locally
- Clone the project.
- Build using maven to import dependencies used in the project.
- Run `ImageRepositoryApplication.kt` class to start the app server.

Or you can test out the live application [here](https://shopify-winter2022-challenge.herokuapp.com/).

Click [here](https://app.swaggerhub.com/Sir-Dave) to see API documentation.