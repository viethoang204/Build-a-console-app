package Controller;

import Model.Claim;

import java.util.ArrayList;
import java.util.List;

public class ClaimManager implements ClaimProcessManager {
    private List<Claim> claims;

    public ClaimManager() {
        this.claims = new ArrayList<>();
    }

    @Override
    public void add(Claim claim) {
        claims.add(claim);
    }

    @Override
    public void update(Claim claim) {
        for (int i = 0; i < claims.size(); i++) {
            if (claims.get(i).getId().equals(claim.getId())) {
                claims.set(i, claim);
                break;
            }
        }
    }

    @Override
    public void delete(String claimId) {
        claims.removeIf(claim -> claim.getId().equals(claimId));
    }

    @Override
    public Claim getOne(String claimId) {
        for (Claim claim : claims) {
            if (claim.getId().equals(claimId)) {
                return claim;
            }
        }
        return null;
    }

    @Override
    public List<Claim> getAll() {
        return claims;
    }
}