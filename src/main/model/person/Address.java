package main.model.person;

public class Address {
    private String street1, street2, postCode, district, state;

    public Address(String street1, String street2, String postCode, String district, String state) {
        this.street1 = street1;
        this.street2 = street2;
        this.postCode = postCode;
        this.district = district;
        this.state = state;
    }

    public String getFullAddress(){
        StringBuilder address = new StringBuilder(street1);

        if(!street2.isEmpty())
            address.append(",\n").append(street2);

        return address.append(",\n").append(postCode)
                .append(", ").append(district)
                .append(",\n").append(state)
                .toString();
    }

    public String toFile(){
        StringBuilder address = new StringBuilder(street1).append(";");

        if(street2.isEmpty())
            address.append("null").append(";");
        else
            address.append(street2).append(";");

        address.append(postCode).append(";");

        if(district.isEmpty())
            address.append("null").append(";");
        else
            address.append(district).append(";");

        return address.append(state).toString();
    }

    public String getStreet1() {
        return street1;
    }

    public String getStreet2() {
        return street2;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }
}
