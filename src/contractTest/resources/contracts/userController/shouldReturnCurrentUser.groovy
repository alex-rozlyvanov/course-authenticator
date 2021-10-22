package contracts.userController

import org.springframework.cloud.contract.spec.Contract
import org.springframework.cloud.contract.spec.internal.HttpHeaders

Contract.make {
    description 'should return current user profile'
    request {
        method GET()
        url('/api/authenticator/users/current') {
            headers {
                header(HttpHeaders.AUTHORIZATION, matching('Bearer ([\\.\\-\\_a-zA-Z0-9]+)'))
            }
        }
    }
    response {
        body(
                "id": anyUuid(),
                "username": anyNonBlankString(),
                "lastName": anyNonBlankString(),
                "enabled": true | false,
                "roles": [$(
                        "id": anyUuid(),
                        "title": anyNonBlankString()
                )]
        )
        status 200
    }
}
