package adapter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class Adapter implements FileOperateInterfaceV2 {

    @NonNull
    private FileOperateInterfaceV1 adaptee;

    @Override
    public List<StaffModel> readAllStaff() {
        return adaptee.readStaffFile();
    }

    @Override
    public void listAllStaff(List<StaffModel> list) {
        adaptee.printStaffFile(list);
    }

    @Override
    public void writeByName(List<StaffModel> list) {
        var res = new ArrayList<>(List.copyOf(list));
        res.sort(Comparator.comparing(StaffModel::getName));
        adaptee.writeStaffFile(res);
    }

    @Override
    public void writeByRoom(List<StaffModel> list) {
        var res = new ArrayList<>(List.copyOf(list));
        res.sort(Comparator.comparing(StaffModel::getRoom));
        adaptee.writeStaffFile(res);
    }
}
