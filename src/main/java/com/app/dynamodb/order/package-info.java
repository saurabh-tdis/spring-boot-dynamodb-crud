@org.springframework.modulith.ApplicationModule(
        displayName = "Order Management",
        allowedDependencies = {"shared", "shared::exception"}
)
package com.app.dynamodb.order;