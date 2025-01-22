package rebue.robotech.vo;

public interface Vo<ID> {
    /**
     * 获取ID
     *
     * @return ID
     */
    default ID getId() {
        throw new UnsupportedOperationException("This method is not yet implemented.");
    }
}
