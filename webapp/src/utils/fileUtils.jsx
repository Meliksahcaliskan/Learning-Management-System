

export const calculateFileSize = (file) => {
    const sizeInBytes = file.size;
    const sizeInMB = sizeInBytes / (1024 * 1024);
    return sizeInMB;

}