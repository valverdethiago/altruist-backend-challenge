package com.altruist.service

import com.altruist.model.Account
import com.altruist.model.Address
import com.altruist.model.State
import com.altruist.repository.AccountRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll
import spock.mock.DetachedMockFactory

@ContextConfiguration(classes = [TestConfig])
class AccountServiceTest extends Specification {
    @Autowired
    AccountRepository mockAccountRepository
    @Autowired
    AddressService addressService
    @Autowired
    AccountService service

    def "Should save account and address"() {
        given: "an account"
        Account account = new Account(
                username: "username123",
                email: "email@example.com",
                address : new Address(
                    name: "Some Name",
                    street: "Some street",
                    city: "Some city",
                    state: State.CA,
                    zipcode: 99999
                )
        )
        UUID expectedAddressId = UUID.randomUUID()
        UUID expectedAccountId = UUID.randomUUID()

        when:
        service.create(account)

        then: "the address is saved"
        1 * addressService.create(account.address) >> expectedAddressId

        and: "the account is saved"
        1 * mockAccountRepository.save(_) >> { Account arg ->
            with(arg){
                username == account.username
                email == account.email
                addressUuid == expectedAddressId
            }

            arg.uuid = expectedAccountId
            arg
        }
    }


    @TestConfiguration
    static class TestConfig {
        DetachedMockFactory factory = new DetachedMockFactory()

        @Bean
        AccountRepository accountRepository() {
            factory.Mock(AccountRepository)
        }

        @Bean
        AddressService addressService() {
            factory.Mock(AddressService)
        }

        @Bean
        AccountService accountService() {
            return new AccountService(accountRepository(), addressService());
        }
    }
}
