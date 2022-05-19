package dev.pw2;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

@Path("/item")
public class CBTester {
    
    private static final Logger LOGGER = Logger.getLogger(CBRService.class);

    @Inject
    CBRService itemRepository;
    private AtomicLong counter = new AtomicLong(0);

    @GET
    public List<Item> coffees() {
        final Long invocationNumber = counter.getAndIncrement();

        maybeFail(String.format("CBRService#items() invocation #%d failed", invocationNumber));

        LOGGER.infof("CBRService#items() invocation #%d returning successfully", invocationNumber);
        return itemRepository.getAllItems();
    }

    private void maybeFail(String failureLogMessage) {
        if (new Random().nextBoolean()) {
            LOGGER.error(failureLogMessage);
            throw new RuntimeException("Resource failure.");
        }
    }

        @Path("/{id}/availability")
        @GET
        public Response availability(int id) {
            final Long invocationNumber = counter.getAndIncrement();
    
            Item item = itemRepository.getItemById(id);
            // check that coffee with given id exists, return 404 if not
            if (item == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
    
            try {
                Integer availability = itemRepository.getAvailability(item);
                LOGGER.infof("ItemResource#availability() invocation #%d returning successfully", invocationNumber);
                return Response.ok(availability).build();
            } catch (RuntimeException e) {
                String message = e.getClass().getSimpleName() + ": " + e.getMessage();
                LOGGER.errorf("CoffeeResource#availability() invocation #%d failed: %s", invocationNumber, message);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(message)
                        .type(MediaType.TEXT_PLAIN_TYPE)
                        .build();
            }
        
    }
}
