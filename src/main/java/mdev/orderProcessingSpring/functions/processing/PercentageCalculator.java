package mdev.orderProcessingSpring.functions.processing;

import mdev.orderProcessingSpring.utils.vars.ErrorCodes;
import mdev.orderProcessingSpring.utils.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author markodevelopment (Mihálovics Márkó)
 */
@Component
public class PercentageCalculator {

    @Autowired
    private ErrorCodes errorCodes;

    /**
     * Called to calculated the validity percentage of an invalid file (~ a file that contains any kind of invalid data)
     * @param size The number of orders and items summed together
     * @param validationErrors The validation errors in the file (NumberFormatExceptions will be ignored - they are totally invalid)
     * @return The percentage
     */
    public float calc(int size, ArrayList<ValidationError> validationErrors){
        float validityPercent = 0.0f;

        if (size != 1){
            int dontCountNumberFormat = 0;

            dontCountNumberFormat = validationErrors.stream()
                    .filter((validationError) -> (validationError.getMessage()
                    .equals("\nError on line " + validationError.getLineNumber() + "\n" +
                        errorCodes.ERROR_NUMBER_FORMAT + "\n")))
                    .map((_item) -> 1).reduce(dontCountNumberFormat, Integer::sum);

            validityPercent = 100.0f - ((float)(validationErrors.size() - dontCountNumberFormat)  /
                    (float)size * 100.0f);
        }

        return validityPercent;
    }

}
