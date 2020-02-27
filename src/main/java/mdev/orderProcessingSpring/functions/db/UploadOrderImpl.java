package mdev.orderProcessingSpring.functions.db;

import mdev.orderProcessingSpring.utils.Item;
import mdev.orderProcessingSpring.utils.Order;
import mdev.orderProcessingSpring.utils.vars.DataBaseVars;
import mdev.orderProcessingSpring.utils.vars.Headers;
import mdev.orderProcessingSpring.utils.OrderDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author markodevelopment (Mihálovics Márkó)
 */
@Component
public class UploadOrderImpl implements OrderDAO {

    @Autowired
    public ValueCounter valueCounter;

    @Autowired
    public Headers headers;

    @Autowired
    public DataBaseVars dataBaseVars;

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private final String INSERT_ROW = "INSERT INTO ";

    /**
     * Overrides (interface) OrderDAOs createOrder method
     * @see OrderDAO#createOrder(Item[], Order)
     *
     * @param items All of the validated rows
     * @param order The order that is being uploaded
     * @return True when the upload is successful
     * @throws ParseException Can throw exception for the DateFormats (very rare, almost impossible)
     */
    @Override
    public boolean createOrder(Item[] items, Order order) throws ParseException {
        String query = INSERT_ROW + dataBaseVars.ORDER_TABLE + "(`" + headers.HEADER_ORDER_ID + "`, " +
                "`" + headers.HEADER_BUYER_NAME + "`, " +
                "`" + headers.HEADER_BUYER_EMAIL + "`, " +
                "`" + headers.HEADER_ORDER_DATE + "`, " +
                "`" + headers.ORDER_TOTAL_VALUE + "`, " +
                "`" + headers.HEADER_ADDRESS + "`, " +
                "`" + headers.HEADER_POSTCODE + "`) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";

        if (valueCounter.getOrderTotalValue(order.getOrderId(), items) > 0){
            return jdbcTemplate.update(query, order.getOrderId(), order.getBuyerName(), order.getBuyerEmail(),
                    (order.getOrderDate().isEmpty() ?
                            new Date(Calendar.getInstance().getTime().getTime()) :
                            new Date(new SimpleDateFormat(dataBaseVars.DATE_FORMAT).parse(order.getOrderDate()).getTime())),
                    valueCounter.getOrderTotalValue(order.getOrderId(), items), order.getAddress(), order.getPostcode()) > 0;
        }

        return false; // No valid items in this order (total order value is 0)
    }
}
