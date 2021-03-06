import java.util.ArrayList;

public class DatasetPerformanceMetrics {
    private float percentage;
    private final ArrayList<ClassLabelAndPercentage> distributionOfFinalInstanceLabels;
    private final ArrayList<LabelAndNumberOfUniqueInstance> labelAndNumberOfUniqueInstances;
    private final int numberOfUserAssigned;
    private final ArrayList<UserAndPercentage> assignedUsersAndCompletenessPercentage;
    private final ArrayList<UserAndPercentage> assignedUsersAndConsistencyPercentage;

    public DatasetPerformanceMetrics(int numberOfUserAssigned) {
        this.percentage = 0;
        this.distributionOfFinalInstanceLabels = new ArrayList<>();
        this.labelAndNumberOfUniqueInstances = new ArrayList<>();
        this.numberOfUserAssigned = numberOfUserAssigned;
        this.assignedUsersAndCompletenessPercentage = new ArrayList<>();
        this.assignedUsersAndConsistencyPercentage = new ArrayList<>();
    }

    public void updatePercentage(ArrayList<Instance> instances) {
        float numOfLabeledInstances = (float) 0.0;
        for (Instance i : instances) {
            if (i.getUserLabels().size() != 0) {
                numOfLabeledInstances++;
            }
        }
        this.percentage = (numOfLabeledInstances / (float) instances.size()) * 100;
    }

    public void updateDistributionOfFinalInstanceLabels(ArrayList<Instance> instances, ArrayList<ClassLabel> classLabels) {

        distributionOfFinalInstanceLabels.clear();
        int[] finalLabelsTable = new int[classLabels.size()];
        int total = 0;
        for (int i : finalLabelsTable)
            i = 0;

        for (Instance instance : instances) {
            if (instance.getInstancePerformanceMetrics().getFinalLabelAndPercentage() == null)
                continue;
            else {
                ClassLabel finalLabel = instance.getInstancePerformanceMetrics().getFinalLabelAndPercentage().getClassLabel();
                for (LabeledInstance userLabel : instance.getUserLabels()) {
                    for (LabelCounter labelCounter : userLabel.getLabels()) {
                        if (labelCounter.getLabel() == finalLabel) {
                            finalLabelsTable[finalLabel.getLabelID() - 1] += labelCounter.getCount();

                        }
                    }
                }
            }
        }

        for (int i : finalLabelsTable)
            total += i;


        for (int i = 0; i < finalLabelsTable.length; i++) {
            if (finalLabelsTable[i] > 0) {
                float percentage = (float) ((finalLabelsTable[i] * 1.0 / total) * 100);
                ClassLabelAndPercentage classLabelAndPercentage = new ClassLabelAndPercentage(classLabels.get(i), percentage);
                distributionOfFinalInstanceLabels.add(classLabelAndPercentage);
            }
        }
    }


    public void updateUniqueInstancesForEachLabel(ArrayList<ClassLabel> classLabels, ArrayList<Instance> instances) {
        labelAndNumberOfUniqueInstances.clear();
        for (ClassLabel classLabel : classLabels) {
            int[] numberOfUniqueInstances = new int[instances.size()];
            for (int numberOfUniqueInstance : numberOfUniqueInstances) {
                numberOfUniqueInstance = 0;
            }
            for (Instance instance : instances) {
                for (LabeledInstance userLabel : instance.getUserLabels()) {
                    for (LabelCounter labelCounter : userLabel.getLabels()) {
                        if (labelCounter.getLabel().getLabelID() == classLabel.getLabelID()) {
                            numberOfUniqueInstances[instance.getID() - 1]++;
                        }
                    }
                }
            }
            int count = 0;
            for (int i = 0; i < numberOfUniqueInstances.length; i++) {
                if (numberOfUniqueInstances[i] > 0) {
                    count++;
                }
            }
            LabelAndNumberOfUniqueInstance labelAndNumberOfUniqueInstance = new LabelAndNumberOfUniqueInstance(classLabel, count);
            labelAndNumberOfUniqueInstances.add(labelAndNumberOfUniqueInstance);
        }
    }

    public void updateAssignedUsersAndCompletenessPercentage(UserInfo userInfo, String datasetName) {
        float newPercentage = 0;

        for (DatasetAndPercentage datasetAndPercentage : userInfo.getUserPerformanceMetrics().getDatasetsCompletenessPercentage()) {
            if (datasetAndPercentage.getDatasetName().equals(datasetName)) {
                newPercentage = datasetAndPercentage.getPercentage();
                break;
            }
        }

        boolean check = false;
        for (UserAndPercentage userAndPercentage : assignedUsersAndCompletenessPercentage) {
            if (userAndPercentage.getUserInfo().equals(userInfo.getUsername())) {
                userAndPercentage.setPercentage(newPercentage);
                check = true;
                break;
            }
        }
        if (!check) {
            UserAndPercentage newUser = new UserAndPercentage(userInfo.getUsername(), newPercentage);
            assignedUsersAndCompletenessPercentage.add(newUser);
        }
    }

    public void updateAssignedUsersAndConsistencyPercentage(UserInfo user) {

        int countOfRecurrent = 0;
        for (LabeledInstance labeledInstance : user.getLabeledInstances())
            if (labeledInstance.getLabels().size() > 1)
                countOfRecurrent++;

        int countOfConsistentInstances = 0;
        for (LabeledInstance labeledInstance : user.getLabeledInstances())
            if (labeledInstance.getLabels().size() == 1 && labeledInstance.getLabels().get(0).getCount() > 1)
                countOfConsistentInstances++;

        countOfRecurrent += countOfConsistentInstances;
        float percentage = 0;
        if (countOfRecurrent == 0) {
            percentage = 0;
        } else {
            percentage = (float) (countOfConsistentInstances * 1.0 / countOfRecurrent) * 100;
        }

        UserAndPercentage userAndPercentage = new UserAndPercentage(user.getUsername(), percentage);

        for (UserAndPercentage andConsistencyPercentage : assignedUsersAndConsistencyPercentage) {
            if (andConsistencyPercentage.getUserInfo().equals(userAndPercentage.getUserInfo())) {
                andConsistencyPercentage = userAndPercentage;
                return;
            }
        }

        assignedUsersAndConsistencyPercentage.add(userAndPercentage);
    }

    public float getPercentage() {
        return percentage;
    }

    public ArrayList<ClassLabelAndPercentage> getDistributionOfFinalInstanceLabels() {
        return distributionOfFinalInstanceLabels;
    }

    public ArrayList<LabelAndNumberOfUniqueInstance> getLabelAndNumberOfUniqueInstances() {
        return labelAndNumberOfUniqueInstances;
    }

    public int getNumberOfUserAssigned() {
        return numberOfUserAssigned;
    }

    public ArrayList<UserAndPercentage> getAssignedUsersAndCompletenessPercentage() {
        return assignedUsersAndCompletenessPercentage;
    }

    public ArrayList<UserAndPercentage> getAssignedUsersAndConsistencyPercentage() {
        return assignedUsersAndConsistencyPercentage;
    }
}
