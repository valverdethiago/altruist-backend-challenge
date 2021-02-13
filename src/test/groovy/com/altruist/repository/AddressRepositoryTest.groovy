package com.altruist.repository

import com.altruist.config.DatabaseConfiguration
import com.altruist.config.RepositoryConfiguration
import com.altruist.model.Address
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Repository
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@ActiveProfiles("test")
@DataJdbcTest(includeFilters = [@ComponentScan.Filter(type = FilterType.ANNOTATION, value = [Repository])])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [DatabaseConfiguration, RepositoryConfiguration])
@Stepwise
@Rollback(true)
class AddressRepositoryTest extends Specification {
    @Autowired
    AddressRepository repository

    @Shared
    Address address

    @Before
    def "Initializes the address"() {
        address = new Address(
                name : "Some name",
                street: "Some street",
                city: "Some city",
                state: "CA",
                zipcode: 99999
        )
    }

    def "Inserts an address"() {
        given: "an address"

        when:
        repository.save(address)

        then: "the address id is returned"
        address.uuid
    }

}
