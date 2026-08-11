package com.eu.habbo.habbohotel.roleplay.apartments;

public class ApartmentOwned {
    private int id;
    private int apartId;
    private int roomId;
    private int lobbyId;
    private int owner;
    private boolean forSale;
    private int price;
    private String paymentType;
    private boolean floorEditor;

    public ApartmentOwned(int id, int apartId, int roomId, int lobbyId, int owner, boolean forSale, int price, String paymentType, boolean floorEditor) {
        this.id = id;
        this.apartId = apartId;
        this.roomId = roomId;
        this.lobbyId = lobbyId;
        this.owner = owner;
        this.forSale = forSale;
        this.price = price;
        this.paymentType = paymentType;
        this.floorEditor = floorEditor;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getApartId() { return apartId; }
    public void setApartId(int apartId) { this.apartId = apartId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getLobbyId() { return lobbyId; }
    public void setLobbyId(int lobbyId) { this.lobbyId = lobbyId; }

    public int getOwner() { return owner; }
    public void setOwner(int owner) { this.owner = owner; }

    public boolean isForSale() { return forSale; }
    public void setForSale(boolean forSale) { this.forSale = forSale; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public boolean isFloorEditor() { return floorEditor; }
    public void setFloorEditor(boolean floorEditor) { this.floorEditor = floorEditor; }
}
