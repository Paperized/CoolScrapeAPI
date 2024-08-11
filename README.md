# DataNotifier
**DataNotifier** is a backend software designed to scrape supported web pages, retrieve data, and deliver it through various channels, including webhooks, sockets, or plain HTTP GET calls. [how to add new scrape procedures](#how-to)

## Key Features
- Scheduled Data Monitoring: The core functionality of DataNotifier revolves around a Scheduler that monitors specific data on web pages using a **trackingId**. This ID replicates the initial call, enabling the detection of any changes in the response. When a change is detected, DataNotifier promptly notifies the caller.

- Customizable Data Filtering: Clients can define resources to scrape and apply additional filters to refine the extracted data. Supported predicates include greater than, less than, equals, AND, OR, and more. Additionally, the service allows sorting by fields and limiting the number of returned elements.

- Versatile Data Delivery: DataNotifier is highly adaptable, capable of sending data via webhooks, sockets, or through simple HTTP GET calls, making it easy to integrate with other systems.

## Intended Use
Originally envisioned as a core service for other backend applications, DataNotifier acts as a centralized tool for scraping, monitoring, and detecting changes in data. It’s ideal for scenarios where real-time updates are crucial, with notifications delivered directly via webhooks or sockets. For more straightforward use cases, it also supports single HTTP GET requests to return scraped data without the need for ongoing monitoring.

## Currently method supported
- [x] Webhook
- [x] HTTP Get
- [ ] Sockets -> useful for pages with high refresh rate on data, not a priority for now

## OpenAPI Generator Usage:

**DataNotifier** utilizes the OpenAPI Generator for several key reasons, enhancing both development efficiency and code maintainability.

### Reasons for Choosing OpenAPI Generator

1.  **Compliance with OpenAPI Specification**: The OpenAPI Generator ensures adherence to the OpenAPI (Swagger) standard. Given a Swagger file, it generates all the necessary interfaces that describe each endpoint using Spring Boot annotations. This approach allows you to focus solely on implementing the business logic by overriding the generated methods, without modifying the generated contract.
    
2.  **Automatic DTO Generation**: The generator automatically creates Data Transfer Objects (DTOs), significantly reducing code boilerplate and minimizing manual effort required to define data structures.
    
3.  **Customizable Code Generation**: The generator's flexibility allows for extensive customization. In this project, custom Mustache templates were added to extend the DTOs with additional methods. Specifically, the custom property `x-dqueriable` was used to introduce filter capabilities directly into the generated DTOs, facilitating complex query operations.
4.  ![image](https://github.com/user-attachments/assets/1f66d07c-3773-4eed-99e0-b46faef7dbe6)
   

### Customization Details

*   **Mustache Templates**: Custom Mustache files were employed to modify the default code generation process. These templates were tailored to enhance DTOs with methods that support advanced filtering, aligning with the project’s specific requirements.
*   ![image](https://github.com/user-attachments/assets/13a63a34-963b-4393-99ac-ab39063b1e9e)


By leveraging OpenAPI Generator, **DataNotifier** achieves a robust, maintainable architecture while streamlining the development process through automated code generation and customizable extensions.

## DQuery: Dynamic Query Filter System:
I did not want to make one filter for each type of data extracted, and for each of them create a new type of filter method using an operation type to switch the type of filter, so I came up with a generic solution that could be used in any Java application, the only needed step is to make the Dtos extends from DQueriables, by using OpenAPI with the custom Mustache this step is also omitted and it's 100% automatic.

**DQuery** is a powerful and flexible filter mechanism designed to build and evaluate complex queries using a hierarchical, tree-like structure. Developed in Java Spring Boot, **DQuery** provides a robust framework for dynamic querying and data retrieval, making it ideal for applications requiring advanced filter capabilities.

### Project Status

**DQuery** is currently a work-in-progress (WIP), but it is functional and designed to be generic enough for use in most scenarios within the project context. The system is effective for filtering elements based on their properties and can be integrated into various data processing tasks.

### Key Features

*   **Hierarchical Query Building**: **DQuery** utilizes a tree-like structure where each node represents a filter condition or operator. This allows for the creation of intricate filter expressions by combining various nodes.
*   **Versatile Filter Types**: Includes a range of filter nodes such as logical operators (`AND`, `OR`, `NOT`), comparison operators (`GTE`, `LTE`, etc.), and placeholders for constants and variables.
*   **Dynamic and Customizable**: Supports dynamic query construction with customizable filter nodes and operators. Extend the default functionality to meet specific needs.

### Performance and Efficiency

*   **No Reflection**: **DQuery** does not use reflection to get the value of a property checked inside a filter, it uses a O(1) fast method automatically generated by OpenAPI.
*   **Memory Efficiency**: By avoiding reflection and utilizing code generation, **DQuery** minimizes memory consumption and avoids the overhead associated with reflection-based approaches. There is no need for initial loading or significant runtime memory usage, leading to more efficient performance.

### Integration with DTOs

To apply **DQuery** filters to your DTOs, you need to ensure that each DTO extends from the `DQueriable` interface. This allows the **DQuery** system to apply filters effectively to the properties of your data objects.

#### Integration Steps

1.  **Extend `DQueriable`**: Modify your DTOs to extend the `DQueriable` interface. This integration is straightforward if you use the OpenAPI Generator with the provided setup:
    *   **Automatic Integration**: By using the same base setup as this project with OpenAPI Generator, you can easily extend your DTOs in one line in the Swagger configuration. This ensures that the necessary methods for filtering are automatically included.
    *   **Manual Implementation**: If not using the OpenAPI Generator, you will need to manually implement the methods required by **DQuery** to enable filtering capabilities on your DTOs.

### Filter Nodes and Components

In **DQuery**, all nodes that need to be evaluated in the filter mechanism must implement the `DQueryCondition` interface. This ensures that each node adheres to the necessary contract for evaluation and integration into the query system.

#### Core Classes and Functions

*   **DQueryNode**: The main class that holds all types of filters, including both logical and comparison nodes. It serves as the base class for defining various filter operations.
    
*   **Filter Functions**:
    
    *   **Logical Nodes**: Include `AND`, `OR`, and `NOT`. These nodes can take one or more `DQueryNode` inputs, allowing for the creation of complex and deeply nested filter chains. Logical nodes enable the combination of multiple filters into a comprehensive query expression.
    *   **Comparison Nodes**: Include operators such as `GTE` (Greater Than or Equal), `LTE` (Less Than or Equal), and others. These nodes generally take one or more specific types of elements, often involving `left` and `right` values, but may vary based on the comparison function.
*   **DComparable**: Functions similarly to a standard comparator. It is used with `DSort` to sort a list based on dynamic properties of the resulting objects. This allows for flexible and customizable sorting based on filter results.
    
*   **DPick**: Used to select only the first X elements from a list in the response. This is useful for pagination or limiting the amount of data returned.
    

#### DQueryRequest

The combination of `DQueryNode`, `DSort`, and `DPick` creates a `DQueryRequest`. This object represents the input for each tracked API and encompasses:

*   **Filtering**: Defined by the `DQueryNode` and its associated logical and comparison nodes.
*   **Sorting**: Managed by `DSort`, which organizes the data based on specified properties.
*   **Picking**: Handled by `DPick`, which limits the number of elements in the response.

#### DQuery Summary

In **DQuery**, to create and evaluate filters:

*   Implement the `DQueryCondition` interface for all nodes.
*   Use `DQueryNode` to define and manage logical and comparison filters.
*   Apply `DComparable`, `DSort`, and `DPick` to customize sorting and selection.
*   Combine these components into a `DQueryRequest` for comprehensive querying capabilities.

## HOW TO

To integrate a new trackable scrappable data type into **DQuery**, follow these steps:

### 1\. Create a New Endpoint

Define a new endpoint in your Swagger API specification to handle the new data type. This endpoint should be designed to accept requests and return responses that include the data you want to scrape.
```
  /tcg-store/find-summary-products/track:
    post:
      tags:
        - tcg-store
      operationId: findSummaryProductsTrack
      parameters:
        - $ref: '#/components/parameters/WebhookUrl'
        - $ref: '#/components/parameters/CheckMode'
        - $ref: '#/components/parameters/SaveMode'
        - $ref: '#/components/parameters/PropertiesToCheck'
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                pageNum:
                  type: integer
                query:
                  type: dquery
      responses:
        '200':
          description: Successful operation
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TcgProductsTracked'
```

### 2\. Define `x-queriable` Parameters

In the Swagger specification for the endpoint, ensure that the returned object includes the `x-queriable` parameter for relevant fields. These parameters are used internally by the system to generate a unique key for each scrapped item. This unique key helps in tracking and managing the data efficiently.

```
    TcgProductsTracked:
      type: object
      properties:
        items:
          items:
            $ref: '#/components/schemas/TcgProductDto'
        track:
          $ref: '#/components/schemas/TrackerInfoDto'

    TcgProductDto:
      type: object
      x-dqueriable:
        id-composition: [ url ]
      properties:
        name:
          type: string
        img:
          type: string
        url:
          type: string
```


### 3\. Implement the Controller

Create a new Controller class that will handle incoming requests to the new endpoint. The Controller should be responsible for receiving requests, processing them, and delegating tasks to the service layer.
```
    @Override
    public ResponseEntity<TcgProductsTracked> findSummaryProductsTrack(String webhookUrl, DOnChangeMode checkMode, DOnChangeSaveMode saveMode, List<String> propertiesToCheck, FindSummaryProductsRequest findSummaryProductsRequest) throws Exception {
        DQueryRequestWebhook queryRequestWebhook = new DQueryRequestWebhook(findSummaryProductsRequest.getQuery());
        queryRequestWebhook.setPreviousDataChecks(DOnChanges.builder()
                .mode(checkMode)
                .saveMode(saveMode)
                .propertiesToCheck(propertiesToCheck)
                .build());

        return ResponseEntity.ok(tcgStoreService.findSummaryProductsTracked(findSummaryProductsRequest.getPageNum(), webhookUrl, queryRequestWebhook));
    }
```

### 4\. Implement the Service

Develop a Service class that contains the business logic for processing the data. The Service should interact with the data source, apply the necessary filters and transformations, and prepare the data for return to the client.
```
    @Override
    public TcgProductsTracked findSummaryProductsTracked(Integer page, String webhookUrl, DQueryRequestWebhook queryRequestWebhook) throws HttpStatusException, UnsuccessfulScrapeException {
        List<TcgProductDto> tcgProductsDto = findSummaryProducts(page, queryRequestWebhook);
        TrackerAction trackerAction = page != null ? TrackerAction.TCGSTORE_SUMMARY_PRODUCTS : TrackerAction.TCGSTORE_ALL_SUMMARY_PRODUCTS;
        String trackerId = productTrackerService.trackNewProduct(tcgStoreScraper.getSummaryProductsUrl(page), WebsiteName.TcgStore, trackerAction, webhookUrl, queryRequestWebhook);

        try {
            productTrackerScheduler.scheduleTracker(trackerId, true); // <- trigger rescheduling
        } catch (TrackingAlreadyScheduledException e) {
            throw new RuntimeException("Inconsistency, Created Tracker with id " + trackerId + " but it's already defined in the in-memory scheduler");
        }

        return new TcgProductsTracked()
                .items(tcgProductsDto)
                .track(new TrackerInfoDto().trackerId(trackerId));
    }
```

### 5\. Update `TrackerAction`

Add a new entry to the `TrackerAction` configuration. This entry should specify:

*   **If the endpoint returns a list**: Indicate whether the endpoint’s response is a list of items.
*   **DTO Class**: Specify the Data Transfer Object (DTO) class that corresponds to the data returned by the endpoint. This helps the tracking system correctly handle and process the data.

```
@Getter
public enum TrackerAction {
    AMAZON_PRODUCT_DETAILS(false, AmazonProductDto.class),
    TCGSTORE_SUMMARY_PRODUCTS(true, TcgProductDto.class),
    TCGSTORE_ALL_SUMMARY_PRODUCTS(true, TcgProductDto.class),
    STEAM_FIND_PROFILE(false, SteamProfileDto.class),
    ADD_NEW_ACTION_HERE(...); <- here you can add new cases, the first parameters indicates if the resulting scrape is a list of a single item
```

### 6\. Implement the Scraping Procedure

Develop the scraping logic to extract data from the endpoint. This procedure should:

*   **Extract the Data**: Implement the logic to scrape or retrieve the data from the specified source.
*   **Return the Data**: Ensure that the extracted data is formatted and returned to the Service class for further processing.

```
@Override
    public List<TcgProductDto> findSummaryAllProducts() throws HttpStatusException, UnsuccessfulScrapeException {
        String url = getSummaryProductsUrl(1);
        if(StringUtils.isBlank(url)) {
            logger.error("Find Summary Products url not found");
            throw new NullPointerException("Find Summary Products url not found");
        }

        return executeScrapeAction(TrackerAction.TCGSTORE_ALL_SUMMARY_PRODUCTS, url); // <- call the @ScrapeAction method, used also by the scheduler to run the same action multiple times
    }

    @ScrapeAction(action = TrackerAction.TCGSTORE_ALL_SUMMARY_PRODUCTS, retryTimes = 1, intervalRetry = 2000) // <- A unique resourse is identified by its TrackerAction and its url. This annotation has also retries and interval retries.
    protected List<TcgProductDto> findSummaryAllProductsInternal(String url) throws HttpStatusException, UnsuccessfulScrapeException { // <- scrape procedure for the TCGSTORE_ALL_SUMMARY_PRODUCTS TrackerAction
        int currPage = -1;
        int nextPage = 1;
        List<TcgProductDto> tcgProductsDto = new ArrayList<>();

        while(currPage != nextPage) {
            currPage = nextPage;

            Document page = scraperHttpService.getPage(getSummaryProductsUrl(currPage), websiteSetting);
            logger.info("Loaded tcg store page: {}", page.location());

            tcgProductsDto.add(getNextItem(page));
        }

        return tcgProductsDto;
    }
```

As you can see it was decided to create a proxy method that internally points to the centralized method for that TrackerAction by using `executeScrapeAction`.

### Summary

By following these steps, you can integrate a new trackable scrappable data type into **DQuery** efficiently. This process involves configuring the API endpoint, defining queryable parameters, implementing controllers and services, and setting up the scraping procedure to handle and process the new data type.
