package mdev.OrderProcessingSpring.functions.processing;

import mdev.OrderProcessingSpring.OPSpringApp;
import mdev.OrderProcessingSpring.functions.db.IdChecker;
import mdev.OrderProcessingSpring.shell.Commands;
import mdev.OrderProcessingSpring.shell.ShellUsrEX;
import mdev.OrderProcessingSpring.utils.DataRow;
import mdev.OrderProcessingSpring.utils.FinalVars;
import mdev.OrderProcessingSpring.utils.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;

@Component
public class Validator {

    @Autowired
    public FinalVars finalVars;

    @Autowired
    public ShellUsrEX shellUsrEX;

    @Autowired
    public Commands commands;

    @Autowired
    public IdChecker idChecker;

    @Autowired
    public ErrorMessageCreator errorMessageCreator;

    @Autowired
    public PercentageCalculator percentageCalculator;

    private ArrayList<ValidationError> validationErrors;

    public String validate(DataRow[] dataRows){
        validationErrors = new ArrayList<>();
        boolean valid = true;

        for (DataRow dr : dataRows){
            dr.disableWarn();
            boolean email = validEmail(dr.getBuyerEmail());
            boolean fill = validFill(dr.getBuyerEmail(), dr.getBuyerName(),
                    dr.getSKU(), dr.getStatus(), dr.getAddress());
            boolean date = true;
            boolean shippingPrice = validShippingPrice(dr.getShippingPrice());
            boolean salePrice = validSalePrice(dr.getSalePrice());
            boolean status = validStatus(dr.getStatus());
            boolean orderItemId = !validOrderItemIdInUse(dr.getOrderItemId());
            boolean orderId = !validOrderIdInUse(dr.getOrderId());
            boolean postcode = dr.getPostcode() != -1;
            boolean lineNumber = dr.getLineNumber() != -1;

            boolean dateIsEmpty = false;
            if (dr.getOrderDate() != null){
                if (dr.getOrderDate().isEmpty()){
                    dateIsEmpty = true;
                }
            }else{
                dateIsEmpty = true;
            }

            if (!dateIsEmpty){
                date = validDate(finalVars.DATE_FORMAT, dr.getOrderDate(), Locale.ENGLISH);
            }

            if (!email || !fill || !date || !shippingPrice || !salePrice ||
                    !status || !orderItemId || !orderId || !postcode || !lineNumber){
                valid = false;
                String errorMessage = errorMessageCreator.create(email, fill, date, shippingPrice,
                        salePrice, status, orderItemId, orderId, dr);
                validationErrors.add(new ValidationError(dr.getLineNumber(),
                        errorMessage, finalVars.STATUS_ERROR));
            }
        }

        if (valid){
            return shellUsrEX.getSuccessMessage("The file is 100% valid!");
        }

        return shellUsrEX.getWarningMessage(Errors(dataRows.length));
    }

    private String Errors(int drSize){
        StringBuilder sb = new StringBuilder();

        sb.append("\n--------------------");
        sb.append("\nThe file is invalid!");
        sb.append("\n--------------------");

        float percent = percentageCalculator.calc(drSize, validationErrors);
        if (percent != 0.0f){
            sb.append("\nBut ");
            sb.append(percent);
            sb.append("% of it is valid.\n");
        }

        for (ValidationError ve : validationErrors){
            sb.append(ve.getMessage());
        }

        return sb.toString();
    }

    private boolean validEmail(String email){
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            OPSpringApp.log.debug(commands.shellUsrEX.getInfoMessage(ex.toString()));
            result = false;
        }
        return result;
    }

    private boolean validFill(String... data){
        boolean correct = true;
        for (String s : data){
            if (s != null){
                if (s.isEmpty()){
                    correct = false;
                }
            }else{
                correct = false;
            }
        }
        return correct;
    }

    private boolean validDate(@SuppressWarnings("SameParameterValue") String format,
                              String value, @SuppressWarnings("SameParameterValue") Locale locale) {
        LocalDateTime ldt;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, locale);

        try {
            ldt = LocalDateTime.parse(value, formatter);
            String result = ldt.format(formatter);
            return result.equals(value);
        } catch (DateTimeParseException dtpe) {
            OPSpringApp.log.debug(commands.shellUsrEX.getInfoMessage(dtpe.toString()));
            try {
                LocalDate ld = LocalDate.parse(value, formatter);
                String result = ld.format(formatter);
                return result.equals(value);
            } catch (DateTimeParseException dtpe1) {
                OPSpringApp.log.debug(commands.shellUsrEX.getInfoMessage(dtpe1.toString()));
                try {
                    LocalTime lt = LocalTime.parse(value, formatter);
                    String result = lt.format(formatter);
                    return result.equals(value);
                } catch (DateTimeParseException dtpe2) {
                    OPSpringApp.log.debug(commands.shellUsrEX.getInfoMessage(dtpe2.toString()));
                }
            }
        }

        return false;
    }

    private boolean validShippingPrice(float price){
        return price >= 0;
    }

    private boolean validSalePrice(float price){
        return price >= 1;
    }

    private boolean validStatus(String status){
        return status.equals(finalVars.STATUS_IN) ||
                status.equals(finalVars.STATUS_OUT);
    }

    private boolean validOrderItemIdInUse(int id) {
        return idChecker.validOrderItemId(id);
    }

    private boolean validOrderIdInUse(int id) {
        return idChecker.validOrderIdInUse(id);
    }

}
