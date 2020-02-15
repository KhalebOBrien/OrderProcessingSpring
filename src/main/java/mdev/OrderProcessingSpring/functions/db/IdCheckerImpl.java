package mdev.OrderProcessingSpring.functions.db;

import mdev.OrderProcessingSpring.utils.FinalVars;
import mdev.OrderProcessingSpring.utils.IdDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author markodevelopment (Mihálovics Márkó)
 */
@Component
public class IdCheckerImpl implements IdDAO {

    @Autowired
    public FinalVars finalVars;

    @Autowired
    public JdbcTemplate jdbcTemplate;

    /**
     * Overrides (interface) IdDAOs validOrderItemId method
     * @see IdDAO#validOrderItemId(int)
     *
     * @param id The ID under review
     * @return False when the OrderItemId is valid (it is not in the database yet)
     */
    @Override
    public boolean validOrderItemId(int id){
        return idExists("SELECT COUNT(1) AS \"" + finalVars.ID_CHECK_RES_AS + "\" FROM " +
                finalVars.ORDER_ITEM_TABLE +
                " WHERE " + finalVars.ORDER_ITEM_ID_COLUMN +
                " = " +
                id);
    }

    /**
     * Overrides (interface) IdDAOs validOrderIdInUse method
     * @see IdDAO#validOrderIdInUse(int)
     *
     * @param id The ID under review
     * @return False when the OrderItemId is valid (it is not in the database yet)
     */
    @Override
    public boolean validOrderIdInUse(int id){
        return idExists("SELECT COUNT(1) AS \"" + finalVars.ID_CHECK_RES_AS + "\" FROM " +
                finalVars.ORDER_TABLE +
                " WHERE " + finalVars.ORDER_ID_COLUMN +
                " = " +
                id);
    }

    /**
     * Overrides (interface) IdDAOs idExists method
     * @see IdDAO#idExists(String)
     *
     * @param query The query to execute
     * @return True when the ID already exists in the database
     */
    @Override
    public boolean idExists(String query){
        //noinspection ConstantConditions
        return jdbcTemplate.query(query, resultSet -> {
            boolean contains = false;
            while(resultSet.next()){
                contains = resultSet.getInt(finalVars.ID_CHECK_RES_AS) == 1;
            }
            return contains;
        });
    }

}
