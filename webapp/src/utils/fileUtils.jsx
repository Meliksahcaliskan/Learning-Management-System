const allowedFileTypes = [
    "application/pdf",
    "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "text/plain"
];

export const calculateFileSize = (file) => {
    const sizeInBytes = file.size;
    const sizeInMB = sizeInBytes / (1024 * 1024);
    return sizeInMB;

}

export const isAllowedFileType = (fileTpye) => {
    return allowedFileTypes.includes(fileTpye);
}