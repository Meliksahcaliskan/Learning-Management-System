
/**
 * Checks if the given date string is in the future.
 * @param {string} dateString - The date string to check.
 * @returns {boolean} - Returns true if the date is in the future, false otherwise.
 */
export const isDateInFuture = (dateString) => {
    const inputDate = new Date(dateString);
    const currentDate = new Date();
    
    // Clear the time part of the current date for comparison
    currentDate.setHours(0, 0, 0, 0);
    
    return inputDate > currentDate;
};
