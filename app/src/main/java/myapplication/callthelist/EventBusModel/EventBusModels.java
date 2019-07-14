package myapplication.callthelist.EventBusModel;

public class EventBusModels {


    public static class DeleteSelectedItem{

        private int id;

        public DeleteSelectedItem(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class UpdateList{
        private int id;

        public UpdateList(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class MakePhoneCall{
        private int id;

        public MakePhoneCall(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
