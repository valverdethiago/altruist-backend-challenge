package com.altruist.service

import com.altruist.model.Account
import com.altruist.model.Address
import com.altruist.model.State
import com.altruist.repository.AddressRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.mock.DetachedMockFactory

@ContextConfiguration(classes = [TestConfig])
class AddressServiceTest extends Specification {
    @Autowired
    AddressRepository mockAddressRepository
    @Autowired
    AddressService service

    def "Should save address"() {
        given: "an address"
        Address address = new Address(
                name: "Some Name",
                street: "Some street",
                city: "Some city",
                state: State.CA,
                zipcode: 99999

        )
        UUID expectedAddressId = UUID.randomUUID()

        when:
        service.create(address)

        then: "the address is saved"
        1 * mockAddressRepository.save(_) >> { Address arg ->
            with(arg){
                name == address.name
                street == address.street
                city == address.city
                state == address.state
                zipcode == address.zipcode
            }

            arg.uuid = expectedAddressId
            arg
        }

    }


    @TestConfiguration
    static class TestConfig {
        DetachedMockFactory factory = new DetachedMockFactory()

        @Bean
        AddressRepository addressRepository() {
            factory.Mock(AddressRepository)
        }

        @Bean
        AddressService addressService(AddressRepository addressRepository) {
            return new AddressService(addressRepository);
        }

    }
}
