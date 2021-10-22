package contracts.userController

import org.springframework.cloud.contract.spec.Contract
import org.springframework.cloud.contract.spec.internal.HttpHeaders

Contract.make {
    description 'should return user profile by id'
    request {
        method GET()
        url(regex('/api/authenticator/users/([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})')) {
            headers {
                header(HttpHeaders.AUTHORIZATION, matching('Bearer ([\\.\\-\\_a-zA-Z0-9]+)'))
            }
        }
    }
    response {
        body(
                "id": "00000000-0000-0000-0000-000000000001",
                "username": anyNonBlankString(),
                "firstName": anyNonBlankString(),
                "lastName": anyNonBlankString(),
                "enabled": true | false,
                "roles": [$(
                        "id": anyUuid(),
                        "title": 'STUDENT'
                ), $(
                        "id": anyUuid(),
                        "title": 'ADMIN'
                ), $(
                        "id": anyUuid(),
                        "title": 'INSTRUCTOR'
                )]
        )
        headers {
            header('Content-Type', 'application/json')
        }
        status 200
    }
}
