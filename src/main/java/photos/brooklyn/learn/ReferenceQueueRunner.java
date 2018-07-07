package photos.brooklyn.learn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReferenceQueueRunner {
    public static void main(String[] args) throws IOException {
        final ReferenceQueue<Person> referenceQueue = new ReferenceQueue<>();
        Person p = new Person();
        final PersonWeakReference ref = new PersonWeakReference(p, referenceQueue);

        // deal with the reference on a separate thread
        final ExecutorService svc = Executors.newSingleThreadExecutor();
        svc.execute(()->{
            try {
                final PersonWeakReference pr = (PersonWeakReference) referenceQueue.remove();
                pr.clean();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // release this object
        p = null;
        System.gc();

        try(
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
        ){
            System.out.println("press any key to exit");
            br.readLine();
        }

    }
}

class PersonCleaner{

    public void clean() {
        System.out.println("Cleaning");
    }
}

class PersonWeakReference extends WeakReference<Person>{

    private final PersonCleaner cleaner;

    public PersonWeakReference(Person referent, ReferenceQueue<? super Person> q) {
        super(referent, q);
        this.cleaner = new PersonCleaner();
    }

    public void clean() {
        this.cleaner.clean();
    }
}
