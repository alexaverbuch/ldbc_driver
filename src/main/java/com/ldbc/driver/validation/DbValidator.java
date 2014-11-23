package com.ldbc.driver.validation;

import com.ldbc.driver.*;
import com.ldbc.driver.runtime.ConcurrentErrorReporter;

import java.util.Iterator;

public class DbValidator {
    public DbValidationResult validate(Iterator<ValidationParam> validationParameters,
                                       Db db) throws WorkloadException {
        DbValidationResult dbValidationResult = new DbValidationResult(db);
        while (validationParameters.hasNext()) {
            ValidationParam validationParam = validationParameters.next();
            Operation<?> operation = validationParam.operation();
            Object expectedOperationResult = validationParam.operationResult();

            OperationHandlerRunnableContext handlerRunner;
            try {
                handlerRunner = db.getOperationHandlerRunnableContext(operation);
            } catch (DbException e) {
                dbValidationResult.reportMissingHandlerForOperation(operation);
                continue;
            }

            OperationResultReport actualOperationResultReport;
            try {
                OperationHandler handler = handlerRunner.operationHandler();
                DbConnectionState dbConnectionState = handlerRunner.dbConnectionState();
                actualOperationResultReport = handler.executeOperation(operation, dbConnectionState);
            } catch (DbException e) {
                dbValidationResult.reportUnableToExecuteOperation(operation, ConcurrentErrorReporter.stackTraceToString(e));
                continue;
            } finally {
                handlerRunner.cleanup();
            }

            Object actualOperationResult = actualOperationResultReport.operationResult();

            if (false == expectedOperationResult.equals(actualOperationResult)) {
                dbValidationResult.reportIncorrectResultForOperation(operation, expectedOperationResult, actualOperationResult);
                continue;
            }

            dbValidationResult.reportSuccessfulExecution(operation);
        }
        return dbValidationResult;
    }
}