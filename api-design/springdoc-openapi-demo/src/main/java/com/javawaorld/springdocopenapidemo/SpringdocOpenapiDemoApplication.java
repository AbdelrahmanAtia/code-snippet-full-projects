package com.javawaorld.springdocopenapidemo;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@SpringBootApplication
public class SpringdocOpenapiDemoApplication {

	@Value("${api.common.version}")          String apiVersion;
	@Value("${api.common.title}")            String apiTitle;
	@Value("${api.common.description}")      String apiDescription;
	@Value("${api.common.termsOfService}")   String apiTermsOfService;
	@Value("${api.common.license}")          String apiLicense;
	@Value("${api.common.licenseUrl}")       String apiLicenseUrl;
	@Value("${api.common.externalDocDesc}")  String apiExternalDocDesc;
	@Value("${api.common.externalDocUrl}")   String apiExternalDocUrl;
	@Value("${api.common.contact.name}")     String apiContactName;
	@Value("${api.common.contact.url}")      String apiContactUrl;
	@Value("${api.common.contact.email}")    String apiContactEmail;

	public static void main(String[] args) {
		SpringApplication.run(SpringdocOpenapiDemoApplication.class, args);
	}

	/**
	 * Will exposed on $HOST:$PORT/swagger-ui.html
	 *
	 * @return the common OpenAPI documentation
	 */
	@Bean
	public OpenAPI getOpenApiDocumentation() {
		return new OpenAPI()
				.info(new Info()
						.title(apiTitle)
						.description(apiDescription)
						.version(apiVersion)
						.contact(new Contact()
								.name(apiContactName)
								.url(apiContactUrl)
								.email(apiContactEmail))
						.termsOfService(apiTermsOfService)
						.license(new License().name(apiLicense).url(apiLicenseUrl)))
				.externalDocs(new ExternalDocumentation()
						.description(apiExternalDocDesc)
						.url(apiExternalDocUrl));
	}
}

//===========================  API DTO ==========================//

class Product {
	private final int productId;
	private final String name;
	private final int weight;

	public Product(int productId, String name, int weight) {

		this.productId = productId;
		this.name = name;
		this.weight = weight;
	}

	public int getProductId() {
		return productId;
	}

	public String getName() {
		return name;
	}

	public int getWeight() {
		return weight;
	}

}

//======================================= Exceptions ===========================================//

class InvalidInputException extends RuntimeException {

	public InvalidInputException() {
	}

	public InvalidInputException(String message) {
		super(message);
	}

	public InvalidInputException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidInputException(Throwable cause) {
		super(cause);
	}
}

class NotFoundException extends RuntimeException {

	public NotFoundException() {
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundException(Throwable cause) {
		super(cause);
	}
}

//============================ APIs ==========================================//

@Tag(name = "ProductComposite", description = "REST API for composite product information.")
interface ProductCompositeService {

	/**
	 * Sample usage: "curl $HOST:$PORT/product-composite/1".
	 *
	 * @param productId Id of the product
	 * @return the composite product info, if found, else null
	 */
	@Operation(summary = "${api.product-composite.get-composite-product.description}", description = "${api.product-composite.get-composite-product.notes}")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
			@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
			@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
			@ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}") })
	@GetMapping(value = "/product-composite/{productId}", produces = "application/json")
	Product getProduct(@PathVariable int productId);
}

@RestController
class ProductCompositeServiceImpl implements ProductCompositeService {

	@Override
	public Product getProduct(int productId) {
		
		if(productId <= 0) {
			throw new InvalidInputException("Invalid productId: " + productId);

		}

		if (productId == 13) {
			throw new NotFoundException("No product found for productId: " + productId);
		}

		return new Product(productId, "fixed product Name", 50);
	}

}

//in real life, this model should be added to a jar and shared between different micro-services
//============================ Utility Models ===========================//

class HttpErrorInfo {
	private final ZonedDateTime timestamp;
	private final String path;
	private final HttpStatus httpStatus;
	private final String message;

	public HttpErrorInfo() {
		timestamp = null;
		this.httpStatus = null;
		this.path = null;
		this.message = null;
	}

	public HttpErrorInfo(HttpStatus httpStatus, String path, String message) {
		timestamp = ZonedDateTime.now();
		this.httpStatus = httpStatus;
		this.path = path;
		this.message = message;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public String getPath() {
		return path;
	}

	public int getStatus() {
		return httpStatus.value();
	}

	public String getError() {
		return httpStatus.getReasonPhrase();
	}

	public String getMessage() {
		return message;
	}
}

//in real life, this exception handler should be added to a jar and shared between different micro-services
//===================== Global Exception Handler =============================//
@RestControllerAdvice
class GlobalControllerExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

	@ResponseStatus(NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)
	public @ResponseBody HttpErrorInfo handleNotFoundExceptions(ServerHttpRequest request, NotFoundException ex) {

		return createHttpErrorInfo(NOT_FOUND, request, ex);
	}

	@ResponseStatus(UNPROCESSABLE_ENTITY)
	@ExceptionHandler(InvalidInputException.class)
	public @ResponseBody HttpErrorInfo handleInvalidInputException(ServerHttpRequest request,
			InvalidInputException ex) {

		return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
	}

	private HttpErrorInfo createHttpErrorInfo(HttpStatus httpStatus, ServerHttpRequest request, Exception ex) {

		final String path = request.getPath().pathWithinApplication().value();
		final String message = ex.getMessage();

		LOG.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
		return new HttpErrorInfo(httpStatus, path, message);
	}
}
