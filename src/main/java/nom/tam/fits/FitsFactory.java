package nom.tam.fits;

/*
 * #%L
 * nom.tam FITS library
 * %%
 * Copyright (C) 2004 - 2015 nom-tam-fits
 * %%
 * This is free and unencumbered software released into the public domain.
 * 
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * #L%
 */

/**
 * This class contains the code which associates particular FITS types with
 * header and data configurations. It comprises a set of Factory methods which
 * call appropriate methods in the HDU classes. If -- God forbid -- a new FITS
 * HDU type were created, then the XXHDU, XXData classes would need to be added
 * and this file modified but no other changes should be needed in the FITS
 * libraries.
 */
public final class FitsFactory {

    private static class FitsSettings {

        private boolean useAsciiTables = true;

        private boolean useHierarch = false;

        private boolean checkAsciiStrings = false;

        private boolean allowTerminalJunk = false;

        private boolean longStringsEnabled = false;

        private FitsSettings copy() {
            FitsSettings settings = new FitsSettings();
            settings.useAsciiTables = useAsciiTables;
            settings.useHierarch = useHierarch;
            settings.checkAsciiStrings = checkAsciiStrings;
            settings.allowTerminalJunk = allowTerminalJunk;
            settings.longStringsEnabled = longStringsEnabled;
            return settings;
        }

    }

    private static final FitsSettings GLOBAL_SETTINGS = new FitsSettings();

    private static final ThreadLocal<FitsSettings> LOCAL_SETTINGS = new ThreadLocal<>();

    private static FitsSettings current() {
        FitsSettings settings = LOCAL_SETTINGS.get();
        if (settings == null) {
            return GLOBAL_SETTINGS;
        } else {
            return settings;
        }
    }

    public static final int FITS_BLOCK_SIZE = 2880;

    private FitsFactory() {
    }

    /**
     * @return Given a Header construct an appropriate data.
     * @param hdr
     *            header to create the data from
     * @throws FitsException
     *             if the header did not contain enough information to detect
     *             the type of the data
     */
    public static Data dataFactory(Header hdr) throws FitsException {

        if (ImageHDU.isHeader(hdr)) {
            Data d = ImageHDU.manufactureData(hdr);
            hdr.afterExtend(); // Fix for positioning error noted by V. Forchi
            return d;
        } else if (RandomGroupsHDU.isHeader(hdr)) {
            return RandomGroupsHDU.manufactureData(hdr);
        } else if (current().useAsciiTables && AsciiTableHDU.isHeader(hdr)) {
            return AsciiTableHDU.manufactureData(hdr);
        } else if (BinaryTableHDU.isHeader(hdr)) {
            return BinaryTableHDU.manufactureData(hdr);
        } else if (UndefinedHDU.isHeader(hdr)) {
            return UndefinedHDU.manufactureData(hdr);
        } else {
            throw new FitsException("Unrecognizable header in dataFactory");
        }

    }

    /**
     * @return Is terminal junk (i.e., non-FITS data following a valid HDU)
     *         allowed.
     */
    public static boolean getAllowTerminalJunk() {
        return current().allowTerminalJunk;
    }

    /**
     * @return Get the current status for string checking.
     */
    static boolean getCheckAsciiStrings() {
        return current().checkAsciiStrings;
    }

    /**
     * @return Get the current status of ASCII table writing
     */
    static boolean getUseAsciiTables() {
        return current().useAsciiTables;
    }

    /**
     * @return <code>true</code> if we are processing HIERARCH style keywords
     */
    public static boolean getUseHierarch() {
        return current().useHierarch;
    }

    /**
     * @return Given Header and data objects return the appropriate type of HDU.
     * @param hdr
     *            the header of the date
     * @param d
     *            the data
     * @param <DataClass>
     *            the class of the data
     * @throws FitsException
     *             if the operation failed
     */
    @SuppressWarnings("unchecked")
    public static <DataClass extends Data> BasicHDU<DataClass> hduFactory(Header hdr, DataClass d) throws FitsException {
        if (d instanceof ImageData) {
            return (BasicHDU<DataClass>) new ImageHDU(hdr, (ImageData) d);
        } else if (d instanceof RandomGroupsData) {
            return (BasicHDU<DataClass>) new RandomGroupsHDU(hdr, (RandomGroupsData) d);
        } else if (current().useAsciiTables && d instanceof AsciiTable) {
            return (BasicHDU<DataClass>) new AsciiTableHDU(hdr, (AsciiTable) d);
        } else if (d instanceof BinaryTable) {
            return (BasicHDU<DataClass>) new BinaryTableHDU(hdr, (BinaryTable) d);
        } else if (d instanceof UndefinedData) {
            return (BasicHDU<DataClass>) new UndefinedHDU(hdr, (UndefinedData) d);
        }
        return null;
    }

    // CHECKSTYLE:OFF
    /**
     * @return Given Header and data objects return the appropriate type of HDU.
     * @param hdr
     *            the header of the date
     * @param d
     *            the data
     * @param <DataClass>
     *            the class of the data
     * @throws FitsException
     *             if the operation failed
     * @deprecated use {@link #hduFactory(Header, Data)} instead
     */
    @Deprecated
    public static <DataClass extends Data> BasicHDU<DataClass> HDUFactory(Header hdr, DataClass d) throws FitsException {
        return hduFactory(hdr, d);
    }

    // CHECKSTYLE:ON

    // CHECKSTYLE:OFF
    /**
     * @return Given an object, create the appropriate FITS header to describe
     *         it.
     * @param o
     *            The object to be described.
     * @throws FitsException
     *             if the parameter could not be converted to a hdu.
     * @deprecated use {@link #hduFactory(Object)} instead
     */
    @Deprecated
    public static BasicHDU<?> HDUFactory(Object o) throws FitsException {
        return hduFactory(o);
    }

    // CHECKSTYLE:ON

    /**
     * @return Given an object, create the appropriate FITS header to describe
     *         it.
     * @param o
     *            The object to be described.
     * @throws FitsException
     *             if the parameter could not be converted to a hdu.
     */
    public static BasicHDU<?> hduFactory(Object o) throws FitsException {
        Data d;
        Header h;

        if (o instanceof Header) {
            h = (Header) o;
            d = dataFactory(h);

        } else if (ImageHDU.isData(o)) {
            d = ImageHDU.encapsulate(o);
            h = ImageHDU.manufactureHeader(d);
        } else if (RandomGroupsHDU.isData(o)) {
            d = RandomGroupsHDU.encapsulate(o);
            h = RandomGroupsHDU.manufactureHeader(d);
        } else if (current().useAsciiTables && AsciiTableHDU.isData(o)) {
            d = AsciiTableHDU.encapsulate(o);
            h = AsciiTableHDU.manufactureHeader(d);
        } else if (BinaryTableHDU.isData(o)) {
            d = BinaryTableHDU.encapsulate(o);
            h = BinaryTableHDU.manufactureHeader(d);
        } else if (UndefinedHDU.isData(o)) {
            d = UndefinedHDU.encapsulate(o);
            h = UndefinedHDU.manufactureHeader(d);
        } else {
            throw new FitsException("Invalid data presented to HDUFactory");
        }

        return hduFactory(h, d);
    }

    /**
     * @return <code>true</code> If long string support is enabled.
     */
    public static boolean isLongStringsEnabled() {
        return current().longStringsEnabled;
    }

    /**
     * Do we allow junk after a valid FITS file?
     * 
     * @param allowTerminalJunk
     *            value to set
     */
    public static void setAllowTerminalJunk(boolean allowTerminalJunk) {
        current().allowTerminalJunk = allowTerminalJunk;
    }

    /**
     * Enable/Disable checking of strings values used in tables to ensure that
     * they are within the range specified by the FITS standard. The standard
     * only allows the values 0x20 - 0x7E with null bytes allowed in one limited
     * context. Disabled by default.
     * 
     * @param checkAsciiStrings
     *            value to set
     */
    public static void setCheckAsciiStrings(boolean checkAsciiStrings) {
        current().checkAsciiStrings = checkAsciiStrings;
    }

    /**
     * Enable/Disable longstring support.
     * 
     * @param longStringsEnabled
     *            value to set
     */
    public static void setLongStringsEnabled(boolean longStringsEnabled) {
        current().longStringsEnabled = longStringsEnabled;
    }

    /**
     * Indicate whether ASCII tables should be used where feasible.
     * 
     * @param useAsciiTables
     *            value to set
     */
    public static void setUseAsciiTables(boolean useAsciiTables) {
        current().useAsciiTables = useAsciiTables;
    }

    /**
     * Enable/Disable hierarchical keyword processing.
     * 
     * @param useHierarch
     *            value to set
     */
    public static void setUseHierarch(boolean useHierarch) {
        current().useHierarch = useHierarch;
    }

    /**
     * Use thread local settings for the curretn thread instead of the lobal
     * ones if the parameter is set to true, else use the shared global
     * settings.
     * 
     * @param useThreadSettings
     *            true if the thread should not share the global settings.
     */
    public static void useThreadLocalSettings(boolean useThreadSettings) {
        if (useThreadSettings) {
            LOCAL_SETTINGS.set(GLOBAL_SETTINGS.copy());
        } else {
            LOCAL_SETTINGS.remove();
        }
    }
}
