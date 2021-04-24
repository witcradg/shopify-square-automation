# Shopify-Square-Automation

## Overview

This application bill be built with Spring Boot and is designed to be run as a REST service.
Data will be sent from the Shopify application to the REST service using a PUT call (not POST).
The REST controller will invoke a method on business service object that will use the Square API to asynchronously (but sequentially) create the customer record* and then create the invoice record.
Once that is done, an SMS service will be invoked to send the customer a message with a link to the invoice.

Shopify -> SSA** -> Square(client)
				-> Square(invoice)
				-> SMS with link to invoice

*What happens if the customer record already exists when he places a second order later?

**Our application: Shopify-Square-Automation

*** 

The REST controller will receive the JSON object as a string from the body of the PUT request.

It will marshal the data into a POJO (possibly an Entity object) passing that object to a service class instance.

The service instance will use a WebClient instance to execute two HTTP Requests. Since the WebClient is designed to invoke API calls asynchronously care will need to be taken to make sure each API call is complete before executing the next call. 

Methods in the WebClient will make two HTTP requests to the Square API. 
The first will create the Customer record.
The second will create the invoice.

Once these two methods are complete a POST request is made to an SMS service like Twilio (there are others). This will require an account. (There may be other solutions, but I haven't researched them.)
