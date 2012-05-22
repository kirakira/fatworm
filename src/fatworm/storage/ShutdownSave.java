package fatworm.storage;

public class ShutdownSave implements Runnable {
    public void run() {
        Storage.getInstance().save();
    }
}
