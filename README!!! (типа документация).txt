1) Запуск приложения:
 Поднять у себя локально базу данных PostgreSQL со следующими properties:
   -port = 5432
   -name = postgres
   -username/login = postgres
   -password = root

Вся инфа продублирована в application.properties

2) Через Liquibase в БД будут созданы следующие сущности:

Client №1 {
    private Long id = 1;
    private String lastName = "Ivanov";
    private String firstName = "Ivan";
    private String middleName = "Ivanovich";
    private String secretWord = "helloword"; (лежит в захешированном значении в БД)
}

Client №2 {
    private Long id = 2;
    private String lastName = "Vasilyev";
    private String firstName = "Vasiliy";
    private String middleName = "Vasilyevich";
    private String secretWord = "worldhello"; (лежит в захешированном значении в БД)
}

Account №1 { - Принадлежит Client №1
    private Long id = 1;
    private Long clientId = 1;
    private Long number = 123;
    private BigDecimal sum = 100000;
    private AccountType type = null; //всегда будет null, так как в ТЗ не было явно указано, какие типы банк.аккаунтов должны быть в приложении :)
    private LocalDateTime registrationDate = ~2018-10-19;
    private LocalDateTime expirationDate = ~2022-10-19;
}

Account №2 { - Принадлежит Client №2
    private Long id = 2;
    private Long clientId = 2;
    private Long number = 321;
    private BigDecimal sum = 100000;
    private AccountType type = null;
    private LocalDateTime registrationDate = ~2019-10-19;
    private LocalDateTime expirationDate = ~2023-10-19;
}


3) Эндпоинты: 
  	Account {
	GET http://localhost:8080/api/v1/accounts/clients/{clientId} - Получить информацию о счетах клиента
 	}

        Client {
        GET http://localhost:8080/api/v1/clients -  Получить информацию обо всех клиентах
	GET http://localhost:8080/api/v1/clients/{clientId} - Получить информацию о клиенте по его идентификатору
	}

	CashOrder {
        GET http://localhost:8080/api/v1/cashorders/accounts/{accountNumber} -  Получить информацию о кассовых ордерах по счету клиента

	POST http://localhost:8080/api/v1/cashorders - Создать кассовый ордер, эндпоинт ждёт на вход:
												    HttpHeader: Secret-Word = helloworld - http-заголовок обязательно!

                                                                                                    JSON: {
                                                                                                            "accountId":1,
													    "orderType":"withdrawal", //Доступные операции: withdrawal/deposit, не чувствителен к регистру
													    "sum":100		
                                                                                                           }
	GET http://localhost:8080/api/v1/cashorders/accounts/{accountNumber} - Получить информацию о кассовых ордерах по счету клиента
	}

	Transaction {
 	GET http://localhost:8080/api/v1/transactions/accounts/{accountNumber} - Получить информацию о транзакциях по счету клиента

        POST http://localhost:8080/api/v1/transactions/inner - Создать перевод между счетами одного пользователя , эндпоинт ждёт на вход:
												    HttpHeader: Secret-Word = helloworld
										                    JSON: {
                                                                                                            "clientId":1,
													    "fromAccount":123,
													    "toAccount":321
										                            "sum":100		
                                                                                                           } 
													(Кстати вылетит 403 FORBIDDEN, так как перевод происходит во внутренней транзакции на чужой счёт, а не между своими счетами)

	POST http://localhost:8080/api/v1/transactions/outer - Создать перевод между счетами разных пользователей, эндпоинт ждёт на вход:
												    HttpHeader: Secret-Word = helloworld
										                    JSON: {
                                                                                                            "clientId":1,
													    "fromAccount":123,
													    "toAccount":321
										                            "sum":100		
                                                                                                           } 
 
 	}


	Если что-то отвалится/не работает/обнаружится "не-баг-а-фича"/будут вопросы, то я 24/7:         email: garrys2machinima@gmail.com
													telegram: t.me/ArbuzovMichael


















