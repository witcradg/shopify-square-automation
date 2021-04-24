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
