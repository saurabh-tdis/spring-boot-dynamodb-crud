@org.springframework.modulith.ApplicationModule(
        displayName = "Product Management",
        allowedDependencies = {"shared", "shared::exception"}
)
package com.app.dynamodb.product;