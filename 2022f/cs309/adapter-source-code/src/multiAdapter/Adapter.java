package multiAdapter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class Adapter implements FileOperateInterfaceV2 {

    @NonNull
    private FileOperateInterfaceV1 fileOperator;

    @NonNull
    private ManageStaffInterface staffManager;

    @Override
    public List<StaffModel> readAllStaff() {
        return fileOperator.readStaffFile();
    }

    @Override
    public void listAllStaff(List<StaffModel> list) {
        fileOperator.printStaffFile(list);
    }

    @Override
    public void writeByName(List<StaffModel> list) {
        var res = new ArrayList<>(List.copyOf(list));
        res.sort(Comparator.comparing(StaffModel::getName));
        fileOperator.writeStaffFile(res);
    }

    @Override
    public void writeByRoom(List<StaffModel> list) {
        var res = new ArrayList<>(List.copyOf(list));
        res.sort(Comparator.comparing(StaffModel::getRoom));
        fileOperator.writeStaffFile(res);
    }

    @Override
    public void addNewStaff(List<StaffModel> list) {
        staffManager.addingStaff(list);
    }

    @Override
    public void removeStaffByName(List<StaffModel> list) {
        staffManager.removeStaff(list);
    }
}
